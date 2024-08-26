package org.hidog.board.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.hidog.board.services.admin.BoardAdminService;
import org.hidog.global.ListData;
import org.hidog.global.constants.DeleteStatus;
import org.hidog.global.exceptions.RestExceptionProcessor;
import org.hidog.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board/admin")
@RequiredArgsConstructor
public class BoardAdminController implements RestExceptionProcessor {

    private final BoardInfoService boardInfoService;
    private final BoardAdminService boardAdminService;

    @GetMapping // 목록 조회
    public JSONData getList(BoardDataSearch search) {
        ListData<BoardData> data = boardInfoService.getList(search, DeleteStatus.ALL);

        return new JSONData(data);
    }

    @PatchMapping("/{mode}") // 목록 수정, 삭제
    public JSONData updatelist(@PathVariable("mode") String mode, @RequestBody RequestAdminList form) {
        List<BoardData> items = boardAdminService.update(mode, form.getItems());
        return new JSONData(items);
    }

    @PatchMapping("/{mode}/{seq}") //게시글 하나 수정, 삭
    public JSONData update(@PathVariable("mode") String mode, @PathVariable("seq") Long seq, RequestBoard form){
        form.setSeq(seq);

        BoardData item = boardAdminService.update(mode, form);

        return new JSONData(item);
    }

    @GetMapping("/info/{seq}")
    public JSONData getInfo(Long seq) {
        BoardData item = boardInfoService.get(seq, DeleteStatus.ALL);

        return new JSONData(item);
    }
}
