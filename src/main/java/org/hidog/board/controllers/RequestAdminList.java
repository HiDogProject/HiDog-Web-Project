package org.hidog.board.controllers;

import lombok.Data;
import org.hidog.board.entities.BoardData;

import java.util.List;

@Data
public class RequestAdminList {
    private List<BoardData> items;
    private BoardData item;
}
