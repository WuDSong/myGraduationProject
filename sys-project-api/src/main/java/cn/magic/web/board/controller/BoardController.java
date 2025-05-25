package cn.magic.web.board.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.board.entity.Board;
import cn.magic.web.board.entity.BoardParam;
import cn.magic.web.board.service.BoardService;
import cn.magic.web.sys_menu.entity.SysMenu;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Board board) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if (board.getIcon().equals(""))
            board.setIcon(null);
        if (board.getSortOrder() > 10 || board.getSortOrder() < 1)
            board.setSortOrder(null);
        System.out.println(board.getCreatorId());
        if (boardService.save(board)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }
    // 申请
    @PostMapping("/apply")
    public ResultVo apply(@RequestBody Board board) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if (board.getIcon().equals(""))
            board.setIcon(null);
        board.setStatus("pending");
        board.setSortOrder(null);
        board.setIsDeleted(null);
        if (boardService.save(board)) {
            return ResultVo.success("申请请求成功!");
        }
        return ResultVo.error("申请失败!");
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

    //分页获取 除了审核状态 后台用
    @GetMapping("/listReviewed")
    public ResultVo getList(BoardParam param) {
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.in("status", Arrays.asList("active", "banned"));
        query.eq("is_deleted", 0);
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Board::getName, param.getName()).orderByAsc(Board::getSortOrder);
        } else query.lambda().orderByAsc(Board::getSortOrder);
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    @GetMapping("/tree")
    public ResultVo getTree() {
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.in("status", Arrays.asList("active", "banned"));
        query.lambda().eq(Board::getIsDeleted,0)
                .orderByAsc(Board::getSortOrder);
        List<Board> boardList = boardService.list(query);
        // 构建树形数据
        Map<Long, Board> boardMap = new LinkedHashMap<>();
        // 先处理父节点
        boardList.stream().filter(board -> board.getParentId() == null)
                .sorted(Comparator.comparing(Board::getSortOrder))
                .forEach(board -> {
                    boardMap.put(board.getBoardId(), board);
                    board.setChildren(new ArrayList<>());
                });
        // 处理子节点
        boardList.stream()
                .filter(board -> board.getParentId() != null)
                .sorted(Comparator.comparing(Board::getSortOrder))
                .forEach(board -> {
                    Board parent = boardMap.get(board.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(board);
                    }
                });

        List <Board> boardTree = new ArrayList<>(boardMap.values());
        return ResultVo.success("查询树形数据成功",boardTree);
    }

    //获取所有区块 (测试用)
    @GetMapping("/getAllList")
    public ResultVo getAll() {
        List<Board> list = boardService.list();
        return ResultVo.success("查询成功", list);
    }

    //获取所有活动版块
    @GetMapping("/activeList")
    public ResultVo getAllActiveList() {
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus, "active").eq(Board::getIsDeleted,0).orderByAsc(Board::getSortOrder);
        List<Board> list = boardService.list(query);
        return ResultVo.success("查询成功", list);
    }
    //获取我的板块
    @GetMapping("/my/{userId}")
    public ResultVo getMyBoard(@PathVariable("userId")Long userId){
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus, "active").eq(Board::getIsDeleted,0)
                .eq(Board::getCreatorId,userId).orderByAsc(Board::getSortOrder);
        List<Board> list = boardService.list(query);
        return ResultVo.success("查询我的板块成功", list);
    }

    //判断是否被占用
    @GetMapping("/isOccupied/{boardName}")
    public ResultVo isOccupied(@PathVariable("boardName") String name) {
        QueryWrapper<Board> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Board::getName, name);
        Board board = boardService.getOne(wrapper);
        if (board != null) {
            return ResultVo.success("被占用！重新填写！", true);
        }
        return ResultVo.success("没有被占用", false);
    }

    // 获取审核版区
    @GetMapping("/getPendingBoardList")
    public ResultVo getPendingBoardList(BoardParam param) {
        //构造分页对象
        IPage<Board> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Board> query = new QueryWrapper<>();
        query.lambda().eq(Board::getStatus,"pending").orderByAsc(Board::getSortOrder);
        //查询
        IPage<Board> list = boardService.page(page, query);
        return ResultVo.success("查询成功", list);
    }
    // 审核通过
    @PutMapping("/resolved")
    public ResultVo resolved(@RequestBody Board board) {
        board.setStatus("banned"); // 默认禁用
        if (boardService.updateById(board)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }
//    获取当前待审版区数目成功
    @GetMapping("/getCountOfPending")
    public ResultVo getCountOfPending() {
        QueryWrapper<Board> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Board::getIsDeleted,0).eq(Board::getStatus,"pending");
        Long num = boardService.count(queryWrapper);
        return ResultVo.success("获取当前待审版区数目成功",num);
    }
}
