# Brief-JSON
A Brief JSON Serializer

JSONSerializer.deserializer :Parse Json text to List and Map;
  JSONSerializer.serializer :Create Json text from List and Map;
      JSONBean.deserializer :Create Java Bean from List and Map;
        JSONBean.serializer :Serialize Java Bean to List and Map;
               JSONSeriable :Mark Field which will be Serialized in Java Bean;
         JSONParseException :Exception include json and position where error occured throwed when parsing a illegal json
