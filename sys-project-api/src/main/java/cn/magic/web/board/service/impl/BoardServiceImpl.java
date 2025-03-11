package cn.magic.web.board.service.impl;


import cn.magic.web.board.entity.Board;
import cn.magic.web.board.mapper.BoardMapper;
import cn.magic.web.board.service.BoardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements BoardService {
}
