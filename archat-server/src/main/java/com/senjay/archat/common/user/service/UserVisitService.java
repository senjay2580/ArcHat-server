package com.senjay.archat.common.user.service;

public interface UserVisitService {

    void addVisitor(String uid);

    String countVisitor(String dateKey);
}
