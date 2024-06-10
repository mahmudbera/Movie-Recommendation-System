/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.datastructureproject3;

import java.lang.System;
import java.util.Arrays;

/**
 *
 * @author Mahmud Bera Karagöz
 */
public class MaxHeap<T extends Comparable<T>> {

    private Node[] heapArray;
    private int maxSize;
    private int currentSize;

    public MaxHeap(int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = 0;
        this.heapArray = new Node[maxSize];
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean isFull() {
        return currentSize == maxSize;
    }

    public void insert(String key, double value) {
        if (isFull()) {
            System.out.println("Heap is full. Cannot insert element.");
            return;
        }

        Node newNode = new Node(key, value);
        heapArray[currentSize] = newNode;
        trickleUp(currentSize);
        currentSize++;
    }

    public Node removeMax() {
        if (isEmpty()) {
            System.out.println("Heap is empty. Cannot remove element.");
            return null;
        }

        Node root = heapArray[0];
        heapArray[0] = heapArray[currentSize - 1];
        currentSize--;
        trickleDown(0);
        return root;
    }

    private void trickleUp(int index) {
        int parent = (index - 1) / 2;
        Node bottom = heapArray[index];

        while (index > 0 && heapArray[parent].getValue() < bottom.getValue()) {
            heapArray[index] = heapArray[parent];
            index = parent;
            parent = (parent - 1) / 2;
        }

        heapArray[index] = bottom;
    }

    private void trickleDown(int index) {
        int largerChild;
        Node top = heapArray[index];

        while (index < currentSize / 2) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (rightChild < currentSize && heapArray[leftChild].getValue() < heapArray[rightChild].getValue()) {
                largerChild = rightChild;
            } else {
                largerChild = leftChild;
            }

            if (top.getValue() >= heapArray[largerChild].getValue()) {
                break;
            }

            heapArray[index] = heapArray[largerChild];
            index = largerChild;
        }

        heapArray[index] = top;
    }

    public void clear() {
        while (!isEmpty()) {
            removeMax();
        }
    }

}
