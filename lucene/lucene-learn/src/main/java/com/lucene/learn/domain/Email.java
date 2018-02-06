package com.lucene.learn.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author zacconding
 * @Date 2018-02-06
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    private String senderEmail;
    private String senderName;
    private String subject;
    private String body;
    private String type;
}
