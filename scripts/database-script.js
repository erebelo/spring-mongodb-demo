// Profile
db = connect("localhost:27017")
db = db.getSiblingDB("demo-db")
db.createCollection("profile")
db.profile.createIndex({userId: 1}, {unique: true, name: "user_id_index"})

// Profile History
db = connect("localhost:27017")
db = db.getSiblingDB("demo-db")
db.createCollection("profile-history")
db.getCollection("profile-history").createIndex({documentId: 1}, {name: "document_id_index"})
db.getCollection("profile-history").createIndex({"document.userId": 1}, {name: "user_id_index"})