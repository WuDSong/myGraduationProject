package cn.magic.web.board.service;

import cn.magic.web.board.entity.Board;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BoardService extends IService<Board> {
    List<Board> searchTopBoardTree(String name);

    List<Board> getAllDescendants(List<Long> topIds);
}