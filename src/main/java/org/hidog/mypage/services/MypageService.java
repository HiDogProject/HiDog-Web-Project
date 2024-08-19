package org.hidog.mypage.services;

import lombok.RequiredArgsConstructor;
import org.hidog.file.entities.FileInfo;
import org.hidog.mypage.repositories.MypageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MypageRepository mypageRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    public FileInfo uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String extension = fileName.substring(fileName.lastIndexOf("."));

        FileInfo fileInfo = FileInfo.builder()
                .gid(userId.toString())
                .location("profile")
                .fileName(fileName)
                .extension(extension)
                .contentType(contentType)
                .build();

        mypageRepository.saveAndFlush(fileInfo);

        long seq = fileInfo.getSeq();
        String uploadDir = uploadPath + "/" + (seq % 10L);
        File dir = new File(uploadDir);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        String uploadPathWithFileName = uploadDir + "/" + seq + extension;
        file.transferTo(new File(uploadPathWithFileName));

        return fileInfo;
    }

    // 프로필 이미지 업데이트 메서드
    public void updateProfileImage(Long memberId, String newImageUrl) {
        FileInfo fileInfo = mypageRepository.findByGid(memberId.toString())
                .orElseThrow(() -> new RuntimeException("File info not found for member: " + memberId));
        fileInfo.setFileUrl(newImageUrl); // fileUrl 필드를 업데이트합니다.
        mypageRepository.save(fileInfo);
    }
}