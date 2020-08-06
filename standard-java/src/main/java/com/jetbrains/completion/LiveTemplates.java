package com.jetbrains.completion;

import com.jetbrains.inspections.entities.Person;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class LiveTemplates {

    public List<Person> getEmployed(final List<Person> people) {
        List<Person> employees = new ArrayList<>();

        for (Person employee : employees) {
            System.out.println("employee = " + employee);
        }

        return employees;
    }

}
