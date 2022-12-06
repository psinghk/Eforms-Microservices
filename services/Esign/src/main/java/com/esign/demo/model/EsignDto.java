package com.esign.demo.model;

import lombok.Data;

@Data
public class EsignDto {
    private String xml;
    private String clientrequestURL;
    private String username;
    private String gatewayURL;

}
 