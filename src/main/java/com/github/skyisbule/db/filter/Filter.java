package com.github.skyisbule.db.filter;

import lombok.Data;

@Data
public class Filter {

    private boolean doFilter;

    private FilterEnum type;

    private String target;

    private int val;

}
