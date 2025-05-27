package cn.magic.web.board.service.impl;


import cn.magic.web.board.entity.Board;
import cn.magic.web.board.mapper.BoardMapper;
import cn.magic.web.board.service.BoardService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BoardServiceImpl extends ServiceImpl<BoardMapper, Board> implements BoardService {

    @Override
    public List<Board> searchTopBoardTree(String name) {
        // 查询匹配的顶级版区
        LambdaQueryWrapper<Board> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .isNull(Board::getParentId)
                .like(StringUtils.isNotBlank(name), Board::getName, name)
                .in(Board::getStatus, Arrays.asList("active", "banned"))
                .eq(Board::getIsDeleted, 0)
                .orderByAsc(Board::getSortOrder);

        return this.list(queryWrapper);
    }

    @Override
    public List<Board> getAllDescendants(List<Long> topIds) {
        if (topIds == null || topIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.baseMapper.selectAllDescendants(topIds);
    }
}