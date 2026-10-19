-- Local development seed data
-- 50 Reservations + 100 ReservationItems
--
-- Reservation IDs: 40001-40050
-- ReservationItem IDs: 50001-50100
--
-- Each reservation:
--   GENERAL x2
--   VIP x1
--
-- Every 10th reservation is CANCELLED.
-- Safe to run repeatedly because conflicts are ignored.


-- =========================================================
-- 1. RESERVATIONS
-- =========================================================

INSERT INTO reservation (
    id,
    user_id,
    event_id,
    status,
    reserved_time,
    total_price
)
SELECT
    40000 + n,

    -- User 30001 -> Reservation 40001
    -- User 30002 -> Reservation 40002
    30000 + n,

    -- Event 10001 -> Reservation 40001
    -- Event 10002 -> Reservation 40002
    10000 + n,

    -- Some cancelled reservations for realistic test data
    CASE
        WHEN n % 10 = 0 THEN 'CANCELLED'
        ELSE 'CONFIRMED'
        END,

    -- All reservation times occur before the first event
    TIMESTAMP '2026-10-20 09:00:00'
        + ((n - 1) * INTERVAL '1 hour'),

    -- GENERAL x2 + VIP x1
    (
SELECT SUM(
    t.price *
    CASE
    WHEN t.ticket_type = 'GENERAL' THEN 2
    WHEN t.ticket_type = 'VIP' THEN 1
    END
    )
FROM ticket t
WHERE t.event_id = 10000 + n
    )

FROM generate_series(1, 50) AS gs(n)

ON CONFLICT DO NOTHING;



-- =========================================================
-- 2. RESERVATION ITEMS
-- =========================================================

INSERT INTO reservation_item (
    id,
    reservation_id,
    ticket_id,
    quantity,
    unit_price
)
SELECT
    50000 + ((n - 1) * 2) + ticket_data.item_offset,

    40000 + n,

    20000 + ((n - 1) * 2) + ticket_data.item_offset,

    ticket_data.quantity,

    t.price

FROM generate_series(1, 50) AS gs(n)

         CROSS JOIN (
    VALUES
        (1, 2),   -- GENERAL x2
        (2, 1)    -- VIP x1
) AS ticket_data(item_offset, quantity)

         JOIN ticket t
              ON t.id =
                 20000 + ((n - 1) * 2) + ticket_data.item_offset

    ON CONFLICT DO NOTHING;



-- =========================================================
-- 3. RECALCULATE REMAINING TICKET QUANTITY
-- =========================================================

-- Direct SQL seed inserts bypass Ticket.decreaseQuantity(),
-- so remaining_quantity must be recalculated.
--
-- CANCELLED reservations do not consume inventory.

UPDATE ticket t
SET remaining_quantity =
        t.total_quantity
            -
        COALESCE(
                (
                    SELECT SUM(ri.quantity)
                    FROM reservation_item ri
                             JOIN reservation r
                                  ON r.id = ri.reservation_id
                    WHERE ri.ticket_id = t.id
                      AND r.status = 'CONFIRMED'
                ),
                0
        );



-- =========================================================
-- 4. KEEP HIBERNATE SEQUENCES AHEAD OF SEEDED IDS
-- =========================================================

SELECT setval(
               to_regclass('reservation_seq'),
               (SELECT COALESCE(MAX(id), 1) FROM reservation),
               true
       )
    WHERE to_regclass('reservation_seq') IS NOT NULL;


SELECT setval(
               to_regclass('reservation_item_seq'),
               (SELECT COALESCE(MAX(id), 1) FROM reservation_item),
               true
       )
    WHERE to_regclass('reservation_item_seq') IS NOT NULL;