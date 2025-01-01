// package com.racha.restdev.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.racha.restdev.model.Account;
// import com.racha.restdev.model.Album;
// import com.racha.restdev.service.AccountService;
// import com.racha.restdev.service.AlbumService;
// import com.racha.restdev.util.constants.Authority;

// @Component
// public class SeedData implements CommandLineRunner {

//     @Autowired
//     private AccountService accountService;

//     @Autowired
//     private AlbumService albumService;

//     @Override
//     public void run(String... args) throws Exception {
//         Account account01 = new Account();
//         Account account02 = new Account();

//         Album album01 = new Album();
//         Album album02 = new Album();
//         Album album03 = new Album();
//         Album album04 = new Album();

//         account01.setEmail("user@user.com");
//         account01.setPassword("password");
//         account01.setAuthorities(Authority.USER.toString());
//         accountService.save(account01);

//         account02.setEmail("admin@admin.com");
//         account02.setPassword("password");
//         account02.setAuthorities(Authority.ADMIN.toString() + " " +
//         Authority.USER.toString());
//         accountService.save(account02);

//         album01.setName("Album 01");
//         album01.setDescription("Description 01");
//         album01.setAccount(account01);

//         album02.setName("Album 02");
//         album02.setDescription("Description 02");
//         album02.setAccount(account01);

//         album03.setName("Album 03");
//         album03.setDescription("Description 03");
//         album03.setAccount(account02);

//         album04.setName("Album 04");
//         album04.setDescription("Description 04");
//         album04.setAccount(account02);

//         albumService.save(album01);
//         albumService.save(album02);
//         albumService.save(album03);
//         albumService.save(album04);
//     }
// }
