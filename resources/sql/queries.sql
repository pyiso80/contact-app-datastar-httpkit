-- Place your queries here. Docs available https://www.hugsql.org/


-- :name get-all-contacts
-- :result many
-- :doc Get all contacts with optional pagination
-- (query-fn :get-all-contacts {:limit 10 :offset 0})
SELECT id, first, last, phone, email
FROM contact
ORDER BY id;