package org.hidog.board.services.admin;

import lombok.RequiredArgsConstructor;
import org.hidog.board.controllers.RequestBoard;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.global.exceptions.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardAdminService {

    private final BoardDataRepository dataRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;

    public List<BoardData> update(String mode, List<BoardData> items){
        if(items == null){
            String modeStr = mode.equals("delete") ? "삭제" : "수정";
            throw new BadRequestException(String.format("%s할 게시글을 선택하세요.", modeStr));
        }

        List<BoardData> updateItems = new ArrayList<>();
        for(BoardData item : items){
            BoardData original = dataRepository.findById(item.getSeq()).orElse(null);
            if(original == null) continue;

            if(mode.equals("delete")){//삭제
                dataRepository.delete(original);
            }else{//수정
                original.setPoster(item.getPoster()); // 작성자
                original.setSubject(item.getSubject()); // 게시글 제목
                original.setContent(item.getContent()); // 게시글 내용
                original.setCategory(item.getCategory()); // 게시판 분류
                original.setEditorView(item.getBoard().isUseEditor()); // 에디터 사용유무

                original.setNum1(item.getNum1());
                original.setNum2(item.getNum2());
                original.setNum3(item.getNum3());

                original.setText1(item.getText1());
                original.setText2(item.getText2());
                original.setText3(item.getText3());

                original.setLongText1(item.getLongText1());
                original.setLongText2(item.getLongText2());
                original.setLongText3(item.getLongText3());

                // 비회원 비밀번호 처리(해시화)
                String guestPw = item.getGuestPw();
                if (StringUtils.hasText(guestPw)) {
                    original.setGuestPw(encoder.encode(guestPw));
                }
                original.setNotice(item.isNotice());
                updateItems.add(original);
            }
        }
        if(mode.equals("delete")){
            dataRepository.flush();
            return items;
        }else{
            dataRepository.saveAllAndFlush(updateItems);
            return updateItems;
        }
    }

    public BoardData update(String mode, BoardData item){
        List<BoardData> items = update(mode, List.of(item));

        return items.get(0);
    }

    public BoardData update(String mode, RequestBoard form){
        BoardData data = modelMapper.map(form, BoardData.class);
        return update(mode,data);

    }

}
