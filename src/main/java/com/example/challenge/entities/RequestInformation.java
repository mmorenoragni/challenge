package com.example.challenge.entities;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "request_information")
public class RequestInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column
    public String urlInformation;

    @Column
    public String responseInformation;

    public RequestInformation() {}

    public RequestInformation(String urlInformation, String responseInformation) {
        this.urlInformation = urlInformation;
        this.responseInformation = responseInformation;
    }

    public long getId() {
        return id;
    }

    public String getUrlInformation() {
        return urlInformation;
    }

    public String getResponseInformation() {
        return responseInformation;
    }

    public String toString() {
        return toJson();
    }

    public String toJson() {
        return String.format("{\"url_information\" : %s, \"reponse_information\" : %s}", urlInformation, responseInformation);
    }
}
