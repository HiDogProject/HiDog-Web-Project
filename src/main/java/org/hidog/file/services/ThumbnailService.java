package org.hidog.file.services;

import lombok.RequiredArgsConstructor;
import org.hidog.file.controllers.RequestThumb;
import org.hidog.file.entities.FileInfo;
import org.hidog.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class ThumbnailService {

    private final FileProperties properties;
    private final FileInfoService infoService; // seq

    /**
     *  썸네일 생성
     *
     * @param form
     * @return
     */
    public String create(RequestThumb form) {
        try {
            Long seq = form.getSeq();
            String url = form.getUrl();
            int width = form.getWidth() == null || form.getWidth() < 10 ? 10 : form.getWidth(); // 값이 없을 때는 10px로 값 고정
            int height = form.getHeight() == null || form.getHeight() < 10 ? 10 : form.getHeight();

            /**
             * 썸네일 이미지가 이미 존재하면 생성을 안하고 경로만 반환
             */
            String thumbPath = getThumbnailPath(seq, url, width, height);
            File _thumbPath = new File(thumbPath);
            if (_thumbPath.exists()) {
                return thumbPath;
            }

            if (seq != null && seq > 0L) { // 파일 등록번호

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 썸네일 경로
     *
     * @param seq
     * @param url
     * @param width
     * @param height
     * @return
     */
    public String getThumbnailPath(Long seq, String url, int width, int height) {
        if (seq == null && !StringUtils.hasText(url)) { // SEQ, URL 중 한개는 필수
            return null;
        }

        String dir = properties.getPath() + "/thumb";
        dir += seq != null ? seq % 10L : Objects.hash(url); // seq : 폴더 생성 // url : 해쉬 생성
        File _dir = new File(dir); // 파일이 없을 경우 파일 생성
        if (!_dir.exists()) { // 재귀적으로 폴더 생성
            _dir.mkdirs();
        }

        if (seq != null) { // seq : 파일 등록 번호 - 1순위
            FileInfo fileInfo = infoService.get(seq); // 파일정보 가져오기
            dir += String.format("/%d_%d_%d%s", width, height, seq, fileInfo.getExtension()); // 너비, 높이, 파일 등록 번호, 파일 확장자 ex) .png
        } else { // URL : 원격 파일 url - 2순위
            String extension = url.substring(url.lastIndexOf(".")); // 확장자
            dir += String.format("/%d_%d_%d%s", width, height, Objects.hash(url), extension); // Objects.hash(url) : 파일명
        }

        return null;
    }
}
