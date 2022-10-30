        DROP TABLE IF EXISTS Author;
        CREATE TABLE Author (
        id int NOT NULL AUTO_INCREMENT,
        name varchar(100) NOT NULL
        );
        ALTER TABLE Author ADD PRIMARY KEY (id);

        DROP TABLE IF EXISTS Blog;
        CREATE TABLE Blog (
        id int NOT NULL AUTO_INCREMENT,
        author_id int NOT NULL,
        blog_name varchar(100) NOT NULL,
        description varchar(100) NOT NULL,
        create_time datetime NOT NULL
        );
        ALTER TABLE Blog ADD PRIMARY KEY (id);
        ALTER TABLE Blog ADD FOREIGN KEY (author_id) REFERENCES Author(id);


        DROP TABLE IF EXISTS Content;
        CREATE TABLE Content (
        id int NOT NULL AUTO_INCREMENT,
        author_id int NOT NULL,
        blog_id int NOT NULL,
        content varchar(100) NOT NULL,
        create_time datetime NOT NULL
        );
        ALTER TABLE Content ADD PRIMARY KEY (id);
        ALTER TABLE Content ADD FOREIGN KEY (blog_id) REFERENCES Blog(id);
        ALTER TABLE Content ADD FOREIGN KEY (author_id) REFERENCES Author(id);