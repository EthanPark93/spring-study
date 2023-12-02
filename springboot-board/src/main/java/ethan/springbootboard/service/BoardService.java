package ethan.springbootboard.service;

import ethan.springbootboard.dto.BoardDTO;
import ethan.springbootboard.entity.BoardEntity;
import ethan.springbootboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// DTO -> Entity (Entiry Class)
// Entity -> DTO (DTO Class)

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();

//        for (BoardEntity boardEntity : boardEntityList) {
//            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
//        }

        boardEntityList.stream().forEach(one -> boardDTOList.add(BoardDTO.toBoardDTO(one)));

        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);

        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);

        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {

        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // 한 페이지당 pageLimit 개씩 글을 보여주고 정렬 기준은 id 기준으로 내림차순 정렬
        // page 위치의 매개변수는 0부터 시작 (인덱스 카운팅)
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        log.info("boardEntities.getContent() = {}", boardEntities.getContent()); // 요청 페이지에 해당하는 글
        log.info("boardEntities.getTotalElements() = {}", boardEntities.getTotalElements()); // 전체 글갯수
        log.info("boardEntities.getNumber() = {}", boardEntities.getNumber()); // DB로 요청한 페이지 번호
        log.info("boardEntities.getTotalPages() = {}", boardEntities.getTotalPages()); // 전체 페이지 갯수
        log.info("boardEntities.getSize() = {}", boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        log.info("boardEntities.hasPrevious() = {}", boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        log.info("boardEntities.isFirst() = {}", boardEntities.isFirst()); // 첫 페이지 여부
        log.info("boardEntities.isLast() = {}", boardEntities.isLast()); // 마지막 페이지 여부

        // 목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));

        return boardDTOS;
    }
}
