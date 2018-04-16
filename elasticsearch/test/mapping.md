# Mapping test

> Mapping  

```
PUT my_index
{
  "mappings": {
    "user": {
      "_all":       { "enabled": false  },
      "properties": {         
        "name":     { "type": "keyword" },
        "age":      { "type": "integer" }  
      }
    }
  }
}
```
