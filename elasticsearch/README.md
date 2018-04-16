# ElasticSearch  + Java API + Spring Data ElasticSearch Learning

# Install

## Centos7

> File descriptor limit  

```
$vi /etc/security/limit.conf  

https://www.elastic.co/guide/en/elasticsearch/reference/current/file-descriptors.html  

#<domain>      <type>  <item>         <value>
app             -        nofile         65536
```  

# Plugins  

## HEAD  
https://github.com/mobz/elasticsearch-head  


> install npm  

```
$yum install epel-release
$yum install npm node.js
$node -v , npm -v
```  

> head download & run  
```
$git clone https://github.com/mobz/elasticsearch-head.git  
$npm install
$npm run start  
```  

> add elasticsearch.yml  

```
http.cors.enabled: true
http.cors.allow-origin: "*"
```  













**ref**

- [시작하세요! 엘라스틱서치 루씬 기반의 실시간 오픈소스 검색엔진](http://book.naver.com/bookdb/book_detail.nhn?bid=8769630)
- [Spring Data ElasticSearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Java API](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html)
- [Elastic git-hub](https://github.com/elastic/elasticsearch)
