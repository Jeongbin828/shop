package inhatc.spring.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inhatc.spring.shop.constant.ItemSellStatus;
import inhatc.spring.shop.entity.Item;
import inhatc.spring.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static inhatc.spring.shop.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional

class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager em;

    public void createItemList(){
        for (int i = 1; i <= 10; i++) {
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000 + i)
                    .stockNumber(100 + i)
                    .itemDetail("테스트 상품 상세 설명" + i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            itemRepository.save(item);
        }
    }

    public void createItemList2(){
        for (int i = 1; i <= 5; i++) {
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000 + i)
                    .stockNumber(100 + i)
                    .itemDetail("테스트 상품 상세 설명" + i)
                    .itemSellStatus(ItemSellStatus.SELL)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            itemRepository.save(item);
        }
        for (int i = 6; i <= 10; i++) {
            Item item = Item.builder()
                    .itemNm("테스트 상품" + i)
                    .price(10000 + i)
                    .stockNumber(100 + i)
                    .itemDetail("테스트 상품 상세 설명" + i)
                    .itemSellStatus(ItemSellStatus.SOLD_OUT)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();

            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("querydsl 테스트2")
    public void querydslTest2(){
        createItemList2();

        BooleanBuilder builder = new BooleanBuilder();
        String itemDetail = "테스트";
        int price = 10002;
        String itemSellStatus = "SELL";

        QItem item = QItem.item;

        builder.and(item.itemDetail.like("%" + itemDetail + "%"));
        builder.and(item.price.gt(price));

        if(StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)){
            builder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);

        Page<Item> page = itemRepository.findAll(builder, pageable);
        List<Item> content = page.getContent();
        content.stream().forEach((e) -> {
            System.out.println(e);
        });
    }

    @Test
    @DisplayName("querydsl 테스트")
    public void querydslTest(){
        createItemTest();
        JPAQueryFactory query = new JPAQueryFactory(em);

        List<Item> itemList = query.selectFrom(item)
                .where(item.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(item.itemDetail.like("%" + "1" + "%"))
                .orderBy(item.price.desc())
                .fetch();

        itemList.forEach((item -> System.out.println(item)));
    }

    @Test
    @DisplayName("Native 테스트")
    public void findByDetailNativeTest(){
        createItemTest();
        itemRepository.findByDetailNative("1")
                .forEach((item) -> {
                    System.out.println(item);
                });
    }

    @Test
    @DisplayName("JPQL 테스트")
    public void findByDetailTest(){
        createItemTest();
        itemRepository.findByDetail("1")
                .forEach((item) -> {
                    System.out.println(item);
                });
    }

    @Test
    @DisplayName("OrderBy 테스트")
    public void findByPriceLessThanOrderByPriceDescTest(){
        createItemTest();
        itemRepository.findByPriceLessThanOrderByPriceDesc(10005)
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("OR 테스트")
    public void findByItemNmOrItemDetailTest(){
        createItemTest();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품2", "테스트 상품 상세 설명8");
        itemList.forEach((item -> {
            System.out.println(item);
        }));
    }

    @Test
    @DisplayName("상품 이름 검색 테스트")
    public void findByItemNmTest(){
        createItemTest();

        itemRepository
                .findByItemNm("테스트 상품1")
                .forEach((item) -> {
                    System.out.println(item);
                });
    }

    @Test
    @DisplayName("상품 생성 테스트")
    public void createItemTest(){
        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .stockNumber(100)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .regTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        System.out.println("========== item : " + item);
        Item savedItem = itemRepository.save(item);
        System.out.println("========== savedItem : " + savedItem);
    }
}