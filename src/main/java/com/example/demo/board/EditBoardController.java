package com.example.demo.board;


import com.example.demo.role.RoleStatus;
import com.example.demo.boradAndUser.NoneUserBoardSaveDataDto;
import com.example.demo.boradAndUser.UserBoardSaveDataDto;
import com.example.demo.security.authentication.AuthenticationManager;
import com.example.demo.security.jwt.JwtManager;
import com.example.demo.security.jwt.TokenStatus;
import com.example.demo.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class EditBoardController {
    @Autowired
    BoardService boardService;
    @Autowired
    UserService userService;
    @Autowired
    EditBoardMapper boardMapper;
    @Autowired
    JwtManager jwtManager;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;

    // @Trace
    @GetMapping("/boards/edit/{boardId}")
    @ApiOperation("유저의 권한에 따라 다른 뷰 반환")
    public String editBoardView(HttpServletRequest request, HttpServletResponse response, @PathVariable Long boardId) throws Exception {

        Map<String,String> manageUrlMap = new HashMap<>();
        manageUrlMap.put(RoleStatus.ROLE_ANONYMOUS.name(),"board/edit/none-user-edit");
        manageUrlMap.put(RoleStatus.ROLE_SITE_USER.name(),"board/edit/user-edit");
        manageUrlMap.put(RoleStatus.ROLE_OAUTH_USER.name(),"board/edit/user-edit");
        manageUrlMap.put(RoleStatus.ROLE_ADMIN.name(),"board/edit/user-edit");
        String refreshToken = jwtManager.getRefreshToken(request);
        TokenStatus isSafeJwt = jwtManager.validation(refreshToken);
        if(isSafeJwt == TokenStatus.NONE) {
            return manageUrlMap.get(RoleStatus.ROLE_ANONYMOUS.name());
        }
        authenticationManager.checkAuthenticationManager(request, response);
        String authenticationView = authenticationManager.getAuthenticationView(request, response, manageUrlMap);
        return authenticationView;
    }

    @PutMapping("/board/none-user/edit/{boardId}")
    @ApiOperation("board - None User 데이터 업데이트")
    @ResponseBody
    public String editBoardData(@RequestBody NoneUserBoardSaveDataDto noneuserBoardSaveDataDto
            ,HttpServletResponse response) throws Exception {
        NoneUserBoardSaveDataDto findBoard = boardService.findNoneUserPasswordBoard(noneuserBoardSaveDataDto.getId());
        System.out.println("findBoard.getPassword() = " + findBoard.getPassword());
        System.out.println("boardSaveDataDto.getPassword() = " + noneuserBoardSaveDataDto.getPassword());
        if(findBoard.getPassword().equals(noneuserBoardSaveDataDto.getPassword())) {
            boardMapper.setNoneUserBoardUpdateDataDto(noneuserBoardSaveDataDto);
            System.out.println("success editBoardData");
            return "ok";
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        throw new RuntimeException("데이터가 다릅니다");
    }

    @PutMapping("/board/user/edit/{pathId}")
    @ApiOperation("board - User 데이터 업데이트")
    @ResponseBody
    public String editUserBoardData(@RequestBody UserBoardSaveViewDto userBoardSaveViewDto,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("?Sfadsfas");

        String refreshToken = jwtManager.getRefreshToken(request);
        UserBoardSaveDataDto userBoardSaveDataDto = boardService.findUserPasswordBoard(userBoardSaveViewDto.getBoardId());
        boardMapper.setUserBoardUpdateDataDto(userBoardSaveViewDto,userBoardSaveDataDto,refreshToken,response);
        return "ok";

    }


}

