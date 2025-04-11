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

-- :name find-contacts
-- :result many
-- (query-fn :find-contacts {:text "m"})
SELECT id, first, last, phone, email
FROM contact
WHERE first ILIKE '%' || :text || '%'
    OR last ILIKE '%' || :text || '%'
    OR phone ILIKE '%' || :text || '%'
    OR email ILIKE '%' || :text || '%'
ORDER BY id;

-- :name find-contact-by-id
-- :result one
-- :doc Get a single contact by ID
-- (query-fn :find-contact-by-id {:id 1})
SELECT id, first, last, phone, email
FROM contact
WHERE id = :id;

-- :name update-contact!
-- :result one
-- :doc Update an existing contact by ID and return the updated row
-- (query-fn :update-contact! {:id 1 :first "Updated" :last "Name" :phone "987-654-3210" :email "updated@example.com"})
UPDATE contact
SET first = :first,
    last  = :last,
    phone = :phone,
    email = :email
WHERE id = :id
RETURNING *;

-- :name delete-contact! :exec
-- :doc Delete a contact by ID
-- (query-fn :delete-contact! {:id 1})
DELETE
FROM contact
WHERE id = :id;