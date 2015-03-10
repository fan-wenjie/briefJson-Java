# Brief-JSON
A Brief JSON Serializer

API:

JSONSerializer.deserializer :Parse Json text to List and Map;

JSONSerializer.serializer :Create Json text from List and Map;

BeanSerializer.deserializer :Create Java Bean from List and Map;

BeanSerializer.serializer :Serialize Java Bean to List and Map;

Class:

Seriable :Mark Field which will be Serialized in Java Bean;

ParseException :Exception include json and position where error occured thrown when parsing a illegal json
