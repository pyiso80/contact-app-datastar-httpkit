-- Place your queries here. Docs available https://www.hugsql.org/


-- :name get-all-contacts
-- :result many
-- :doc Get all contacts with optional pagination
-- (query-fn :get-all-contacts {:limit 10 :offset 0})
SELECT id, first, last, phone, email
FROM contact
ORDER BY id;

-- :name save-contact!
-- :result one
-- :doc Insert a new contact and return the inserted row
-- (query-fn :save-contact! {:first "Tom" :last "Müller" :phone "123-456-8888" :email "tom.müller@example.com"})
INSERT INTO contact (first, last, phone, email)
VALUES (:first, :last, :phone, :email)
RETURNING *;