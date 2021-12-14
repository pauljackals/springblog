package net.pauljackals.springblog.domain.helpers;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StateFileInfo<T> {
    private Class<T> beanClass;
    private Map<String, String> mapping;
}
