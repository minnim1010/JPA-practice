package jpa.dto;

import jpa.entity.item.Item;

import java.util.List;

public record CategoryResponse(Long id, String name, List<Item> items) {
}
