package com.chorifa.secondkillproject.service;

public interface SequenceService {

    String SEQ_VALUE_KEY = "seq.cur.value";

    long SEQ_STEP = 1;

    String generateOrderId();

    String generateOrderIdWithRedis();
}
