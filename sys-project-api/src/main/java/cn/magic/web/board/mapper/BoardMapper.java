package cn.magic.web.board.mapper;

import cn.magic.web.board.entity.Board;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BoardMapper extends BaseMapper<Board> {
    @Select({
            "<script>",
            "WITH RECURSIVE cte AS (",
            "SELECT board_id, parent_id FROM board WHERE parent_id IN ",
            "<foreach collection='topIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            " UNION ALL ",
            "SELECT b.board_id, b.parent_id FROM board b ",
            "INNER JOIN cte ON b.parent_id = cte.board_id",
            ") SELECT * FROM board WHERE board_id IN (SELECT board_id FROM cte)",
            "</script>"
    })
    List<Board> selectAllDescendants(@Param("topIds") List<Long> topIds);
}
