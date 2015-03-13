# Brief-JSON
A Brief JSON Serializer

API:

JSONSerializer.deserializer :Parse Json text to List and Map;

JSONSerializer.serializer :Create Json text from List and Map;

BeanSerializer.deserializer :Create Java Bean from List and Map;

BeanSerializer.serializer :Serialize Java Bean to List and Map;

Class:

Seriable :Mark Field which will be Serialized in Java Bean;

ParseException :Exception include json and position where error occured thrown when parsing an illegal json

JSON TEXT <====JSONSerializer=====>Map,List,String,etc. <====BeanSerializer====>JAVA BEAN

## 大道至简

轻量级JSON解析库Brief－JSON，追求以最少的代码完成JSON解析及JAVA对象的序列化和反序列化。用户只需把代码文件拷贝到自己的项目中即可使用。

采用模块化设计，将JSON序列化与JAVA Bean序列化分成2个包，使用户按需拷贝代码文件。

序列化流程

JSON TEXT <====JSONSerializer=====>Map,List,String,etc. <====BeanSerializer====>JAVA BEAN

BeanSerializer.deserializer :把List和Map反序列化为Java Bean;

JSONSerializer.serializer :把List和Map序列化为JSON文本;

JSONSerializer.deserializer :将JSON文本解析为List和Map

BeanSerializer.serializer :把Java Bean序列化为List和Map

Seriable :把Java Bean中要序列化的字段用此注解。无此注解的字段将不序列化;

ParseException :解析JSON时报错抛出的异常。