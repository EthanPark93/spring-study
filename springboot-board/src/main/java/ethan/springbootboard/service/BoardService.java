package ethan.springbootboard.service;

import ethan.springbootboard.dto.BoardDTO;
import ethan.springbootboard.entity.BoardEntity;
import ethan.springbootboard.entity.BoardFileEntity;
import ethan.springbootboard.repository.BoardFileRepository;
import ethan.springbootboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// DTO -> Entity (Entiry Class)
// Entity -> DTO (DTO Class)

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        // 파일 첨부에 따라 로직 분리
        if (boardDTO.getBoardFile().isEmpty()) {
            // 첨부 파일 없음.
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 첨부 파일 있음.
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름을 가져옴
                3. 서버 저장용 이름을 만듦    // 내사진.jpg => 9823469_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();

            BoardEntity board = boardRepository.findById(savedId).get();

            for (MultipartFile boardFile : boardDTO.getBoardFile()) {
//                MultipartFile boardFile = boardDTO.getBoardFile(); // 1번.
                String originalFilename = boardFile.getOriginalFilename(); // 2번.
//            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String storedFileName = UUID.randomUUID() + "_" + originalFilename; // 3번.
                String savePath = "/Users/ethan/springboot_img/" + storedFileName; // 4번. 맥 기준 경로
                boardFile.transferTo(new File(savePath)); // 5번.

                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }

        }
    }

    @Transactional
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
