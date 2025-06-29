package com.justteam.test_quest_api.api.file;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {
    private final Bucket firebaseBucket;


    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("파일이 비어있습니다.");
        }

        // 파일 이름 고유하게 생성 (UUID 사용)
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Firebase Storage에 파일 업로드
        // uploadFrom이라는 blob 명칭이 있다면 file.getInputStream()을 사용해야 합니다.
        Blob blob = firebaseBucket.create(fileName, file.getInputStream(), file.getContentType());

        // 공개 다운로드 URL 반환 (Firebase Storage 규칙에 따라 공개 접근이 허용되어야 함)
        // blob.getMediaLink()는 더 이상 추천되지 않으므로, signed URL을 생성하는 것이 일반적입니다.
        // 또는 Firebase Storage 콘솔에서 파일 URL을 직접 확인할 수도 있습니다.
        // 간편하게 직접적인 public URL을 구성하는 방법 (보안에 주의!)
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                firebaseBucket.getName(),
                fileName);
    }
}
