package com.github.skyisbule.db.filter;

import lombok.Data;

import java.util.List;

@Data
public class FilterChain {

    private List<Filter> filters;

}
