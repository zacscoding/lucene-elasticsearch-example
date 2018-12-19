# ELK demo  

### <a href="#web-server">Web server</a>  

- apache webserver
- logstash  

### <a href="#log-server">Log server</a>  

- elasticsearch
- kibana

---  

<div id="web-server"></div>  

> ## Web server  

- apahce  
- logstash  

#### Apache2  

> 설치

```
$ sudo apt-get update
$ sudo apt-get install apache2
```  

> timestamp  

```
$ vi /etc/apache2/envvars

## 추가
export TZ='Asia/Seoul'
```  

```
$ /etc/init.d/apache2 restart
```  

> json format 추가  

```
$ vi /etc/apache2/apache2.conf

## 로그스태시 read용 파일 로그 포맷 추가
LogFormat "{ \"time\":\"%t\", \"clientip\":\"%a\",\"host\":\"%V\", \"request\":\"%U\", \"query\":\"%q\", \"method\":\"%m\", \"status\":\"%>s\", \"userAgent\":\"%{User-agent}i\", \"referer\":\"%{Referer}i\" }" json_format
```  

```
$ vi /etc/apache2/sites-enabled/000-default.conf

## 위에 추가 한 json format 파일
CustomLog ${APACHE_LOG_DIR}/access_json.log json_format
```

```
$ /etc/init.d/apache2 restart
```  

> 로그 파일 확인  

```
$ tail -f /var/log/apache2/access_json.log

> http://ip 접속
```    

#### Logstash  

> 로그 스태시 설치  

```
https://www.elastic.co/kr/downloads/logstash
```  

> 로그스태시 설정  

```
$ logstash-apache.conf  

input{
        file{
                path=>"/var/log/apache2/access_json.log"
                type=>apache
                codec=>json
                start_position => beginning
        }
}
filter{
        geoip{source=>"clientip"}
}
output{
        elasticsearch{
                hosts=>"192.168.5.78:9200"
        }
        stdout { codec => rubydebug }
}
```  

> 로그 스태시 시작  

```
$ ./bin/logstash -f logstash-apache.conf
Sending Logstash's logs to /home/app/logstash/logstash-6.2.4/logs which is now configured via log4j2.properties
[2018-12-19T00:40:21,158][INFO ][logstash.modules.scaffold] Initializing module {:module_name=>"netflow", :directory=>"/home/app/logstash/logstash-6.2.4/modules/netflow/configuration"}
[2018-12-19T00:40:21,235][INFO ][logstash.modules.scaffold] Initializing module {:module_name=>"fb_apache", :directory=>"/home/app/logstash/logstash-6.2.4/modules/fb_apache/configuration"}
[2018-12-19T00:40:21,862][WARN ][logstash.config.source.multilocal] Ignoring the 'pipelines.yml' file because modules or command line options are specified
[2018-12-19T00:40:22,075][INFO ][logstash.agent           ] No persistent UUID file found. Generating new UUID {:uuid=>"a0cd9679-b6a5-4c34-89af-1ce1e8502e58", :path=>"/home/app/logstash/logstash-6.2.4/data/uuid"}
[2018-12-19T00:40:22,816][INFO ][logstash.runner          ] Starting Logstash {"logstash.version"=>"6.2.4"}
[2018-12-19T00:40:23,449][INFO ][logstash.agent           ] Successfully started Logstash API endpoint {:port=>9600}
[2018-12-19T00:40:27,265][INFO ][logstash.pipeline        ] Starting pipeline {:pipeline_id=>"main", "pipeline.workers"=>2, "pipeline.batch.size"=>125, "pipeline.batch.delay"=>50}
[2018-12-19T00:40:28,326][INFO ][logstash.outputs.elasticsearch] Elasticsearch pool URLs updated {:changes=>{:removed=>[], :added=>[http://192.168.5.78:9200/]}}
[2018-12-19T00:40:28,344][INFO ][logstash.outputs.elasticsearch] Running health check to see if an Elasticsearch connection is working {:healthcheck_url=>http://192.168.5.78:9200/, :path=>"/"}
[2018-12-19T00:40:28,951][WARN ][logstash.outputs.elasticsearch] Restored connection to ES instance {:url=>"http://192.168.5.78:9200/"}
[2018-12-19T00:40:29,097][INFO ][logstash.outputs.elasticsearch] ES Output version determined {:es_version=>6}
[2018-12-19T00:40:29,103][WARN ][logstash.outputs.elasticsearch] Detected a 6.x and above cluster: the `type` event field won't be used to determine the document _type {:es_version=>6}
[2018-12-19T00:40:29,169][INFO ][logstash.outputs.elasticsearch] Using mapping template from {:path=>nil}
[2018-12-19T00:40:29,237][INFO ][logstash.outputs.elasticsearch] Attempting to install template {:manage_template=>{"template"=>"logstash-*", "version"=>60001, "settings"=>{"index.refresh_interval"=>"5s"}, "mappings"=>{"_default_"=>{"dynamic_templates"=>[{"message_field"=>{"path_match"=>"message", "match_mapping_type"=>"string", "mapping"=>{"type"=>"text", "norms"=>false}}}, {"string_fields"=>{"match"=>"*", "match_mapping_type"=>"string", "mapping"=>{"type"=>"text", "norms"=>false, "fields"=>{"keyword"=>{"type"=>"keyword", "ignore_above"=>256}}}}}], "properties"=>{"@timestamp"=>{"type"=>"date"}, "@version"=>{"type"=>"keyword"}, "geoip"=>{"dynamic"=>true, "properties"=>{"ip"=>{"type"=>"ip"}, "location"=>{"type"=>"geo_point"}, "latitude"=>{"type"=>"half_float"}, "longitude"=>{"type"=>"half_float"}}}}}}}}
[2018-12-19T00:40:29,365][INFO ][logstash.outputs.elasticsearch] Installing elasticsearch template to _template/logstash
[2018-12-19T00:40:29,538][INFO ][logstash.outputs.elasticsearch] New Elasticsearch output {:class=>"LogStash::Outputs::ElasticSearch", :hosts=>["//192.168.5.78:9200"]}
```  

---  


<div id="log-server"></div>  

> ## Log server  

- elasticsearch
- kibana  

## Kibana  

> 설정

```
$ vi config/kibana.yml

# 외부에서 접속 할 수 있도록
server.host: "0.0.0.0
```  

```
http://kibanaip.5601 접속  
```
