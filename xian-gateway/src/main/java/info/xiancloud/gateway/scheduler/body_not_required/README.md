### body not required
不需要入参的完整的http body作为透传参数————入参非透传。程序会将http body按照约定的格式进行解析：
- 按照x-www-form-url-encoded格式解析
- 按照json object格式进行解析

