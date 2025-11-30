# Family Tree Drag & Drop Design

## 📚 Frontend Library Recommendation

### **Recommended: React D3 Tree + React DnD**

**Option 1: react-d3-tree (Recommended)**
- ✅ **Pros:**
  - Built specifically for tree visualization
  - Beautiful, customizable tree layouts
  - Good performance with large trees
  - Active maintenance
  - Easy to integrate drag & drop
  - Support for horizontal/vertical layouts
  
- ❌ **Cons:**
  - Requires D3 knowledge for advanced customization
  - Larger bundle size

**Installation:**
```bash
npm install react-d3-tree react-dnd react-dnd-html5-backend
```

**Option 2: react-family-tree**
- ✅ **Pros:**
  - Specifically designed for family trees
  - Pre-built family tree components
  - Good for genealogy apps
  
- ❌ **Cons:**
  - Less flexible
  - Smaller community

**Option 3: Custom with react-dnd + react-flow**
- ✅ **Pros:**
  - Maximum flexibility
  - Modern, beautiful UI
  - Great for complex relationships
  
- ❌ **Cons:**
  - More development time
  - Steeper learning curve

### **Final Recommendation: react-d3-tree + react-dnd**

**Why:**
- Best balance of features and ease of use
- Great for family tree visualization
- Easy to add drag & drop
- Good documentation
- Active community

---

## 🎨 UI/UX Design

### Tree Layout Options:
1. **Horizontal (Top-Down)** - Traditional family tree
2. **Vertical (Left-Right)** - Modern, space-efficient
3. **Radial** - Circular, centered on root person

### Drag & Drop Interactions:
1. **Move Person** - Change parent/child relationship
2. **Add Relationship** - Drag to create new relationship
3. **Remove Relationship** - Drag person away to remove
4. **Change Generation** - Move up/down generations

---

## 🔧 Backend API Design

### 1. Get Family Tree Structure
```
GET /api/v1/family-trees/{treeId}/structure
```

### 2. Update Relationship (Drag & Drop)
```
PUT /api/v1/family-trees/{treeId}/relationships
```

### 3. Move Person (Change Parent)
```
PUT /api/v1/family-trees/{treeId}/persons/{personId}/parent
```

### 4. Update Person Position
```
PUT /api/v1/family-trees/{treeId}/persons/{personId}/position
```

---

## 📊 Data Structure

### Tree Node Structure:
```json
{
  "id": 1,
  "name": "John Doe",
  "avatar": "url",
  "gender": "MALE",
  "dateOfBirth": "1990-01-01",
  "generationLevel": 0,
  "children": [...],
  "spouses": [...],
  "parents": [...]
}
```

### Drag & Drop Payload:
```json
{
  "personId": 1,
  "newParentId": 2,
  "relationshipType": "BIOLOGICAL",
  "position": { "x": 100, "y": 200 }
}
```

