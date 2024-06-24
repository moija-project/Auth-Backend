package com.example.loginservice.client;

import com.example.loginservice.config.MultiPartConfig;
import com.example.loginservice.dto.PostReq;
import com.example.loginservice.etity.Account;
import com.example.loginservice.global.BaseResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@FeignClient(name = "post", url = "${spring.cloud.openfeign.client.post-url}",configuration = MultiPartConfig.class)
@Qualifier("post")
public interface PostClient{
    @PostMapping(value="/post/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    Object writePost(
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @RequestPart(value = "userId") String userId
    );

    @PutMapping(value="/post/write/{postId}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    Object editPost(
            @PathVariable(name="postId") Long postId,
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "write") PostReq.PostWriteReq postWriteReq,
            @RequestPart(value = "userId") String userId
    );

    @DeleteMapping(value = "/post/delete/{postId}",consumes = MULTIPART_FORM_DATA_VALUE)
    Object deletePost(
            @PathVariable(name="postId") Long postId,
            @RequestPart(value = "userId") String userId
    );
    @GetMapping(value = "/post/list", produces = "application/json")
    Object getList(@RequestParam(value = "category",required = false) String category,
                   @RequestParam(value = "view_type",required = false) String viewType,
                   @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo);
    @GetMapping(value = "/post/search", produces = "application/json")
    Object getSearch(
            @RequestParam(value = "view_type",required = false) String viewType,
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "search_type",required = false) String searchType,
            @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo
    );
    @GetMapping(value = "/post/page", produces = "application/json")
    Object getViewPost(@RequestParam(value = "post_id") Long postId);
    @PostMapping(value = "/post/page", produces = "application/json")
    Object getViewPostAuth(@RequestParam(value = "post_id") Long postId,
                       @RequestParam(value = "user_id") String userId);

    @PostMapping(value = "/post/like", consumes = MULTIPART_FORM_DATA_VALUE)
    Object likePost(
            @RequestPart(name = "req") PostReq.PostLikeReq postLikeReq,
            @RequestPart(name = "userId") String userId
    );
    @PostMapping(value ="/post/clip", consumes = MULTIPART_FORM_DATA_VALUE)
    Object clipPost(
            @RequestPart(name = "req") PostReq.PostClipReq postClipReq,
            @RequestPart(name = "userId") String userId
    );
    @PostMapping("/post/question/{postId}")
    Object viewQuestion(
            @PathVariable(name="postId") Long postId,
            @RequestBody String userId
    );
    @PostMapping(value = "/post/waiting/{postId}", consumes = MULTIPART_FORM_DATA_VALUE)
    Object writeAnswer(
            @PathVariable(name="postId") Long postId,
            @RequestPart(value = "req") PostReq.PostWaitingReq postWaitingReq,
            @RequestPart(value = "userId") String userId
    );
    @PostMapping("/post/renew/{postId}")
    Object renew(
            @PathVariable(name = "postId") Long postId,
            @RequestBody String userId
    );
    @PostMapping("/post/stop/{postId}")
    Object stopRecruit(
            @PathVariable(name = "postId") Long postId,
            @RequestBody String userId
    );
    @PostMapping("/post/start/{postId}")
    Object startRecruit(
            @PathVariable(name = "postId") Long postId,
            @RequestBody String userId
    );
    @PostMapping(value="/post/grant/{postId}",consumes = MULTIPART_FORM_DATA_VALUE)
    Object grantPost(
            @PathVariable(value = "postId") Long postId,
            @RequestPart(name = "score") String score,
            @RequestPart(name = "userId") String userId
    );
    @GetMapping("/post/title/{postId}")
    Object titlePost(
            @PathVariable(value = "postId")Long postId
    );
    @GetMapping("/post/picture/{postId}")
    Object picturePost(
            @PathVariable(value = "postId") Long postId
    );
}
