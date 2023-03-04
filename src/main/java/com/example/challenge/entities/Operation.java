package com.example.challenge.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import java.io.Serializable;

@Entity(name = "operation")
public class Operation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private double firstNum;
    @Column(nullable = false, length = 100)
    private double secondNum;

    public Operation() {}

    public Operation(double firstNum, double secondNum) {
        this.firstNum = firstNum;
        this.secondNum = secondNum;
    }

    public Long getId() {
        return id;
    }

    public double getFirstNum() {
        return firstNum;
    }

    public double getSecondNum() {
        return secondNum;
    }
}
