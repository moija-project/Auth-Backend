package com.example.loginservice.controller;

import com.example.loginservice.client.PostClient;
import com.example.loginservice.dto.PostReq;
import com.example.loginservice.dto.PostRes;
import com.example.loginservice.dto.QnADTO;
import com.example.loginservice.etity.Account;
import com.example.loginservice.global.BaseException;
import com.example.loginservice.global.BaseResponse;
import com.example.loginservice.global.BaseResponseStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


//scheduled remover 필요!! 현재 지워진 글은 비트만 바꿔서 안보이게 하고 있으므로, 1달에 한번씩 데이터베이스 삭제, 3달 이상 지난 사용 불가능 포스트 삭제!!!


@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostClient postClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SpringFormEncoder formEncoder;


    @PostMapping(value="/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object writePost(
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @AuthenticationPrincipal Account account
            ) throws BaseException {
        if(account == null) {
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        }

        return postClient.writePost(images,postWriteReq, account.getUsername());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
//        multipartBodyBuilder.part("write", objectMapper.writeValueAsString(postWriteReq), MediaType.APPLICATION_JSON);
//        multipartBodyBuilder.part("userId",account.getUsername());
//        if(images != null) {
//            for (MultipartFile image : images)
//                multipartBodyBuilder.part("image", image.getResource(), MediaType.IMAGE_JPEG);
//        }
//
//        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
//        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
//
//        return restTemplate.postForEntity(postUrl+"/post/write", httpEntity,
//                String.class);

    }

    @PatchMapping(value="/write/{postId}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Object editPost(
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @AuthenticationPrincipal Account account,
            @PathVariable(name = "postId") Long postId
    ) throws JsonProcessingException, BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.editPost(postId, images,postWriteReq, account.getUsername());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
//        multipartBodyBuilder.part("write", objectMapper.writeValueAsString(postWriteReq), MediaType.APPLICATION_JSON);
//        multipartBodyBuilder.part("userId",account.getUsername());
//        if(images != null) {
//            for (MultipartFile image : images)
//                multipartBodyBuilder.part("image", image.getResource(), MediaType.IMAGE_JPEG);
//        }
//
//        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();
//        HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);
//
//        return restTemplate.postForEntity(postUrl+"/post/write/"+postId, httpEntity,
//                String.class);
    }

    @DeleteMapping("/delete/{postId}")
    public Object deletePost(
            @PathVariable(name="postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws URISyntaxException, BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.deletePost(postId, account.getUsername());
    }

    @GetMapping("/list")
    public Object loadPostList(
            @RequestParam(value="category",required = false) String category,
            @RequestParam(value = "view_type",required = false) String viewType,
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "search_type",required = false) String searchType,
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo
    ) {
        return postClient.getList
                (category,viewType,keyword,searchType,pageNo);
    }

    @GetMapping("/page")
    public Object viewPost(
            @RequestParam(value = "post_id") Long postId
    ) throws BaseException, IOException {
        return postClient.getViewPost(postId);
    }
    @PostMapping("/page")
    public Object viewPostAuth(
            @RequestParam(value = "post_id") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException, IOException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.getViewPostAuth(postId, account.getUsername());
    }



    @PostMapping("/like")
    public Object likePost(
            @RequestBody PostReq.PostLikeReq postLikeReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.likePost(postLikeReq,account.getUsername());
    }

    @PostMapping("/clip")
    public Object clipPost(
            @RequestBody PostReq.PostClipReq postClipReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.clipPost( postClipReq,account.getUsername());
    }

    @PostMapping("/question/{postId}")
    public Object viewQuestion(
            @PathVariable(name="postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException, IOException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.viewQuestion(postId, account.getUsername());
    }

    @PostMapping("/waiting/{postId}")
    public Object writeAnswer(
            @PathVariable(name="postId") Long postId,
            @RequestBody PostReq.PostWaitingReq postWaitingReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.writeAnswer(postId,postWaitingReq, account.getUsername());
    }

    @PostMapping("/renew/{postId}")
    public Object renew(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.renew(postId, account.getUsername());
    }
    @PostMapping("/stop/{postId}")
    public Object stopRecruit(
            @PathVariable(value = "postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.stopRecruit(postId,account.getUsername());
    }
    @PostMapping("/start/{postId}")
    public Object startRecruit(
            @PathVariable(value = "postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.startRecruit(postId,account.getUsername());
    }

    @PostMapping(value = "/grant/{postId}")
    public Object grantPost(
            @PathVariable(value = "postId") Long postId,
            @RequestBody Map<String,String> score,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(BaseResponseStatus.LOGIN_FIRST);
        return postClient.grantPost(postId,score.get("score"), account.getUsername() );
    }

    @GetMapping("/title/{postId}")
    public Object titlePost(
            @PathVariable(value = "postId") Long postId
    ) {
        return postClient.titlePost(postId);
    }
}
