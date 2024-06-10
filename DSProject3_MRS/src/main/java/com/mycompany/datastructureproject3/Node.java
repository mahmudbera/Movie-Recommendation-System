/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.datastructureproject3;

/**
 *
 * @author Mahmud Bera Karagöz
 */
public class Node {
    private String key;
    private double value;

    public Node(String key, double value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
