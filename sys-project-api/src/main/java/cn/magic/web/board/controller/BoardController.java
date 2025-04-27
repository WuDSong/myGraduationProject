package cn.magic.web.board.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.board.entity.Board;
import cn.magic.web.board.entity.BoardParam;
import cn.magic.web.board.service.BoardService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Board board) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if(board.getIcon().equals(""))
            board.setIcon(null);
        if (boardService.save(board)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Board board) {
        if (boardService.updateById(board)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (boardService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }
    //分页获取
    @GetMapping("/list")
    public ResultVo getList(BoardParam param){
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Board::getName, param.getName());
        }
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }
    //获取所有
    @GetMapping("/getAllList")
    public ResultVo getAll(){
        List<Board> list = boardService.list();
        return ResultVo.success("查询成功", list);
    }
    //判断是否被占用
    @GetMapping("/isOccupied/{boardName}")
    public ResultVo isOccupied(@PathVariable("boardName") String name){
        QueryWrapper<Board> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Board::getName, name);
        Board board = boardService.getOne(wrapper);
        if (board != null) {
            return ResultVo.success("被占用！重新填写！",true);
        }
        return ResultVo.success("没有被占用",false);
    }

}
