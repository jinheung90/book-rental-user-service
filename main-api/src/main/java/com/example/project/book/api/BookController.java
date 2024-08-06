package com.example.project.book.api;


import com.example.project.auth.dto.UserProfileDto;

import com.example.project.auth.service.UserService;
import com.example.project.book.client.api.NaverBookSearchClient;
import com.example.project.book.client.dto.NaverBookSearchDto;
import com.example.project.book.dto.SearchBookDto;
import com.example.project.book.dto.UserBookDto;
import com.example.project.book.service.BookService;

import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;
    private final NaverBookSearchClient naverBookSearchClient;
    private final UserService userService;

    @GetMapping("/books")
    public ResponseEntity<Page<SearchBookDto>> searchBooks(
            @Parameter(description = "페이지")
            @RequestParam(name = "page") int page,
            @Parameter(description = "사이즈")
            @RequestParam(name = "size") int size,
            @Parameter(description = "정렬 (DESC, ASC)")
            @RequestParam(name = "direction", defaultValue = "DESC", required = false) String direction,
            @Parameter(description = "정렬 키워드 (updatedAt) 추가 가능")
            @RequestParam(name = "sortKey", defaultValue = "updatedAt", required = false) String sortKey,
            @Parameter(description = "이름 (나중에는 키워드 검색 예정)")
            @RequestParam(name = "name", required = false) String name,
            @Parameter(description = "유저 아이디")
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        // TODO 검색 엔진으로 변경
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), sortKey);
        final Page<UserBookDto> searchResult = bookService.pageBooks(pageRequest, name, userId);

        final List<Long> userIds = searchResult.getContent().stream().map(UserBookDto::getUserId).toList();
        final Map<Long, UserProfileDto> userProfileDtoMap = userService.getUserProfilesByUserIds(userIds);
        final List<SearchBookDto> result = searchResult.getContent().stream()
                .map(userBookDto -> new SearchBookDto(userBookDto, userProfileDtoMap.get(userBookDto.getUserId())))
                .toList();
        return ResponseEntity.ok(new PageImpl<>(result, pageRequest, searchResult.getTotalElements()));
    }

    @GetMapping("/books/naver")
    public ResponseEntity<NaverBookSearchDto> getBooksFromNaver(
            @Parameter(description = "오프셋") @RequestParam(name = "start") int start,
            @Parameter(description = "보여주는 개수") @RequestParam(name = "display") int display,
            @RequestParam(name = "name") String name
    ) {
       final NaverBookSearchDto result = naverBookSearchClient.getBooksFromName(start, display, name);
       return ResponseEntity.ok(result);
    }
}
