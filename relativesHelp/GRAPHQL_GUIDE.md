# GraphQL Guide

## GraphQL Endpoints

- **GraphQL Endpoint**: `http://localhost:8080/graphql`
- **GraphiQL UI**: `http://localhost:8080/graphiql` (Interactive query editor)

## Cách sử dụng GraphiQL

1. Khởi động application
2. Mở browser: http://localhost:8080/graphiql
3. Viết queries/mutations trong editor
4. Click "Play" để execute

## Authentication

Để sử dụng GraphQL với authentication, thêm header:

```json
{
  "Authorization": "Bearer <your-jwt-token>"
}
```

Trong GraphiQL, thêm vào "HTTP HEADERS" section:
```json
{
  "Authorization": "Bearer <your-token>"
}
```

## Example Queries

### 1. Register User

```graphql
mutation {
  register(input: {
    username: "testuser"
    email: "test@example.com"
    password: "password123"
    fullName: "Test User"
  }) {
    token
    user {
      id
      username
      email
    }
  }
}
```

### 2. Login

```graphql
mutation {
  login(input: {
    emailOrUsername: "test@example.com"
    password: "password123"
  }) {
    token
    user {
      id
      username
      email
    }
  }
}
```

### 3. Get Current User

```graphql
query {
  me {
    id
    username
    email
    fullName
    roles
  }
}
```

### 4. Create Family Tree

```graphql
mutation {
  createFamilyTree(input: {
    name: "Họ Nguyễn"
    description: "Gia phả họ Nguyễn"
    isPublic: false
  }) {
    id
    name
    description
    createdAt
  }
}
```

### 5. Create Person

```graphql
mutation {
  createPerson(input: {
    familyTreeId: 1
    fullName: "Nguyễn Văn A"
    gender: MALE
    dateOfBirth: "1990-01-01"
    placeOfBirth: "Hà Nội"
    isAlive: true
    generationLevel: 1
  }) {
    id
    fullName
    gender
    dateOfBirth
    generationLevel
  }
}
```

### 6. Get Persons in Family Tree

```graphql
query {
  persons(familyTreeId: 1) {
    id
    fullName
    gender
    dateOfBirth
    generationLevel
    biography
  }
}
```

### 7. Create Relationship

```graphql
mutation {
  createRelationship(input: {
    familyTreeId: 1
    personId: 1
    relatedPersonId: 2
    relationshipType: FATHER
  }) {
    id
    relationshipType
    person {
      fullName
    }
    relatedPerson {
      fullName
    }
  }
}
```

### 8. Get Relationships

```graphql
query {
  relationships(personId: 1, familyTreeId: 1) {
    id
    relationshipType
    person {
      fullName
    }
    relatedPerson {
      fullName
    }
  }
}
```

### 9. Search Persons

```graphql
query {
  searchPersons(familyTreeId: 1, keyword: "Nguyễn") {
    id
    fullName
    biography
  }
}
```

### 10. Create Event

```graphql
mutation {
  createEvent(input: {
    familyTreeId: 1
    personId: 1
    eventTypeId: 1
    title: "Sinh nhật"
    eventDate: "2024-12-25"
    isRecurring: true
    reminderDays: 7
  }) {
    id
    title
    eventDate
    eventType {
      name
      colorCode
    }
  }
}
```

### 11. Get Upcoming Events

```graphql
query {
  upcomingEvents(familyTreeId: 1, days: 30) {
    id
    title
    eventDate
    eventTime
    eventType {
      name
      colorCode
    }
    participants {
      person {
        fullName
      }
      rsvpStatus
    }
  }
}
```

### 12. Upload Media

```graphql
mutation {
  uploadMedia(input: {
    familyTreeId: 1
    personId: 1
    fileName: "photo.jpg"
    originalFileName: "IMG_1234.jpg"
    fileType: "image/jpeg"
    fileSize: 2048576
    cloudStorageUrl: "https://s3.amazonaws.com/bucket/photo.jpg"
    description: "Family photo"
    tags: ["family", "gathering"]
  }) {
    id
    fileName
    cloudStorageUrl
    createdAt
  }
}
```

### 13. Get Media

```graphql
query {
  media(familyTreeId: 1) {
    id
    fileName
    fileType
    cloudStorageUrl
    thumbnailUrl
    description
    tags
    createdAt
  }
}
```

### 14. Complex Query - Family Tree với Persons và Relationships

```graphql
query {
  familyTree(id: 1) {
    id
    name
    description
  }
  persons(familyTreeId: 1) {
    id
    fullName
    gender
    dateOfBirth
    relationships: relationships(personId: id, familyTreeId: 1) {
      relationshipType
      relatedPerson {
        fullName
      }
    }
  }
}
```

## Best Practices

1. **Chỉ query fields cần thiết**: GraphQL cho phép bạn chỉ lấy data cần thiết
2. **Sử dụng fragments**: Để tái sử dụng field sets
3. **Aliases**: Khi cần query cùng field với parameters khác nhau
4. **Variables**: Sử dụng variables thay vì hardcode values

### Example với Variables:

```graphql
query GetPerson($id: ID!) {
  person(id: $id) {
    id
    fullName
    biography
  }
}
```

Variables:
```json
{
  "id": 1
}
```

### Example với Fragments:

```graphql
fragment PersonDetails on Person {
  id
  fullName
  gender
  dateOfBirth
  biography
}

query {
  person(id: 1) {
    ...PersonDetails
  }
  persons(familyTreeId: 1) {
    ...PersonDetails
  }
}
```

## Error Handling

GraphQL trả về errors trong response:

```json
{
  "data": null,
  "errors": [
    {
      "message": "Person not found",
      "path": ["person"],
      "extensions": {
        "code": "NOT_FOUND"
      }
    }
  ]
}
```

## Schema Introspection

Bạn có thể query schema:

```graphql
query {
  __schema {
    types {
      name
      fields {
        name
        type {
          name
        }
      }
    }
  }
}
```

Hoặc sử dụng GraphiQL's "Docs" panel để xem schema.

