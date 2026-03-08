package com.stream;

import com.model.academic.Result;

import java.util.List;

public class ResultStreamQueries {
    public List<Result> filterResultsByTeacherID(List<Result> results, Long id){
        return results.stream()
                .filter(r -> r.getAClass() != null
                        && r.getAClass().getTeacher() != null
                        && r.getAClass().getTeacher().getTeacherID().equals(id))
                .toList();
    }
}
