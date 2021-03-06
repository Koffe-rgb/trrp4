toc.dat                                                                                             0000600 0004000 0002000 00000022422 13764676135 0014463 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        PGDMP                           x            trrp4db    12.3    12.3 %    [           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false         \           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false         ]           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false         ^           1262    41206    trrp4db    DATABASE     �   CREATE DATABASE trrp4db WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Russian_Russia.1251' LC_CTYPE = 'Russian_Russia.1251';
    DROP DATABASE trrp4db;
                postgres    false                     2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
                postgres    false         _           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                   postgres    false    3         �            1259    41237    heroes    TABLE     �   CREATE TABLE public.heroes (
    id integer NOT NULL,
    id_user integer NOT NULL,
    name text NOT NULL,
    health integer NOT NULL
);
    DROP TABLE public.heroes;
       public         heap    postgres    false    3         �            1259    41235    heroes_id_seq    SEQUENCE     �   CREATE SEQUENCE public.heroes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.heroes_id_seq;
       public          postgres    false    205    3         `           0    0    heroes_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.heroes_id_seq OWNED BY public.heroes.id;
          public          postgres    false    204         �            1259    41277    phrases    TABLE     n   CREATE TABLE public.phrases (
    id integer NOT NULL,
    phrase text NOT NULL,
    type integer NOT NULL
);
    DROP TABLE public.phrases;
       public         heap    postgres    false    3         �            1259    41275    phrases_id_seq    SEQUENCE     �   CREATE SEQUENCE public.phrases_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.phrases_id_seq;
       public          postgres    false    3    209         a           0    0    phrases_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.phrases_id_seq OWNED BY public.phrases.id;
          public          postgres    false    208         �            1259    41253 	   statistic    TABLE     �   CREATE TABLE public.statistic (
    id integer NOT NULL,
    id_user integer NOT NULL,
    wins integer NOT NULL,
    loses integer NOT NULL
);
    DROP TABLE public.statistic;
       public         heap    postgres    false    3         �            1259    41251    statistic_id_seq    SEQUENCE     �   CREATE SEQUENCE public.statistic_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.statistic_id_seq;
       public          postgres    false    207    3         b           0    0    statistic_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.statistic_id_seq OWNED BY public.statistic.id;
          public          postgres    false    206         �            1259    41209    users    TABLE     �   CREATE TABLE public.users (
    id integer NOT NULL,
    login text NOT NULL,
    hash text NOT NULL,
    salt text NOT NULL,
    nickname text NOT NULL
);
    DROP TABLE public.users;
       public         heap    postgres    false    3         �            1259    41207    users_id_seq    SEQUENCE     �   CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public          postgres    false    3    203         c           0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public          postgres    false    202         �
           2604    41240 	   heroes id    DEFAULT     f   ALTER TABLE ONLY public.heroes ALTER COLUMN id SET DEFAULT nextval('public.heroes_id_seq'::regclass);
 8   ALTER TABLE public.heroes ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    205    204    205         �
           2604    41280 
   phrases id    DEFAULT     h   ALTER TABLE ONLY public.phrases ALTER COLUMN id SET DEFAULT nextval('public.phrases_id_seq'::regclass);
 9   ALTER TABLE public.phrases ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    208    209    209         �
           2604    41256    statistic id    DEFAULT     l   ALTER TABLE ONLY public.statistic ALTER COLUMN id SET DEFAULT nextval('public.statistic_id_seq'::regclass);
 ;   ALTER TABLE public.statistic ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    207    206    207         �
           2604    41212    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public          postgres    false    203    202    203         T          0    41237    heroes 
   TABLE DATA           ;   COPY public.heroes (id, id_user, name, health) FROM stdin;
    public          postgres    false    205       2900.dat X          0    41277    phrases 
   TABLE DATA           3   COPY public.phrases (id, phrase, type) FROM stdin;
    public          postgres    false    209       2904.dat V          0    41253 	   statistic 
   TABLE DATA           =   COPY public.statistic (id, id_user, wins, loses) FROM stdin;
    public          postgres    false    207       2902.dat R          0    41209    users 
   TABLE DATA           @   COPY public.users (id, login, hash, salt, nickname) FROM stdin;
    public          postgres    false    203       2898.dat d           0    0    heroes_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.heroes_id_seq', 1, false);
          public          postgres    false    204         e           0    0    phrases_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.phrases_id_seq', 44, true);
          public          postgres    false    208         f           0    0    statistic_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.statistic_id_seq', 5, true);
          public          postgres    false    206         g           0    0    users_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.users_id_seq', 11, true);
          public          postgres    false    202         �
           2606    41245    heroes heroes_pk 
   CONSTRAINT     N   ALTER TABLE ONLY public.heroes
    ADD CONSTRAINT heroes_pk PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.heroes DROP CONSTRAINT heroes_pk;
       public            postgres    false    205         �
           2606    41285    phrases phrases_pk 
   CONSTRAINT     P   ALTER TABLE ONLY public.phrases
    ADD CONSTRAINT phrases_pk PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.phrases DROP CONSTRAINT phrases_pk;
       public            postgres    false    209         �
           2606    41258    statistic statistic_pk 
   CONSTRAINT     T   ALTER TABLE ONLY public.statistic
    ADD CONSTRAINT statistic_pk PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.statistic DROP CONSTRAINT statistic_pk;
       public            postgres    false    207         �
           2606    41217    users users_pk 
   CONSTRAINT     L   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pk;
       public            postgres    false    203         �
           1259    41218    users_login_uindex    INDEX     L   CREATE UNIQUE INDEX users_login_uindex ON public.users USING btree (login);
 &   DROP INDEX public.users_login_uindex;
       public            postgres    false    203         �
           2606    41246    heroes heroes_users_id_fk    FK CONSTRAINT     x   ALTER TABLE ONLY public.heroes
    ADD CONSTRAINT heroes_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id);
 C   ALTER TABLE ONLY public.heroes DROP CONSTRAINT heroes_users_id_fk;
       public          postgres    false    203    205    2762         �
           2606    41259    statistic statistic_users_id_fk    FK CONSTRAINT     ~   ALTER TABLE ONLY public.statistic
    ADD CONSTRAINT statistic_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id);
 I   ALTER TABLE ONLY public.statistic DROP CONSTRAINT statistic_users_id_fk;
       public          postgres    false    2762    207    203                                                                                                                                                                                                                                                      2900.dat                                                                                            0000600 0004000 0002000 00000000005 13764676135 0014261 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        \.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           2904.dat                                                                                            0000600 0004000 0002000 00000013361 13764676135 0014276 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        41	Бросив надежду поразить каменное сердце противника, attacker стреляет в голову.	0
42	attacker взвешивает свои шансы и бьёт противника самым тяжёлым	0
44	«Вспышка справа!» — кричит attacker. defender заученным движением падает на землю, где и получает богатырский пинок под рёбра.	0
19	attacker вдруг заметил, что его оружие воспламенилось, и, не мешкая, нанес сокрушительный удар по противнику. god не покидает своего героя	-1
25	god сдвигает солнце за спину своему герою и ослепленный defender пропускает мощный удар	-1
16	god рисует красивую радугу и спускает по ней своему подопечному лечилку. defender обиженно сопит, на оздоровляющегося врага	1
13	Луч света пролился на поле боя. attacker воспрял духом, чувствуя, что god рядом	1
32	attacker берёт противника за душу. defender чувствует, как сердце рвётся из груди.	0
33	Окутавшись божественным сиянием, attacker забывает про раны.	1
35	Вспышка, раскат грома — и attacker ехидно усмехается над новой причёской своего противника.	-1
40	attacker тянет носочек, потом пяточку. defender нелепо трясёт вывихнутой ступнёй.	0
31	god рисует красивую радугу и спускает по ней своему подопечному лечилку.	1
26	defender профессионально просчитывает наперёд действия противника и, ловко уворачиваясь, попадает под удар, который по-дилетантски наносит attacker.	0
28	attacker делает из противника образцово-наказательный пример.	0
27	Крикнув уборщице «Это defender мусорит на арене!», attacker отбегает в сторону и наблюдает за расправой.	0
4	Главное в этой схватке не победа. Хотя нет, главное attacker	0
29	attacker поднимает соперника над головой и бросает в сектор своих болельщиков. defender с трудом выползает из-под града ударов обратно на арену, сверкая свежими ушибами.	0
30	attacker наступает на горло песне противника и вырезает глаголы-связки.	0
34	attacker рявкнул «defender — бить!» и вогнал противника по колено в землю.	0
43	attacker крепко пожал противнику руку	0
37	Табло арены ярко вспыхнуло и взорвалось. defender ослеплен вспышкой и ранена осколками.	-1
8	Не найдя нож, attacker резкими воплями режет уши противника	0
7	attacker делает сопернику точечный массаж челюсти, убавляя тому красоты и здоровья	0
5	attacker старательно втирает урон кулаками в спину противника	0
9	attacker рукоприкладствует не покладая рук	0
10	attacker угощает соперника конфетой, после чего наблюдает, как того мучает кариес	0
11	Подбирая оптимальный градус отношений, attacker бросает противника то в жар, то в холод 	0
12	attacker бросает тень на противника. defender  страдает от недостатка витамина D	0
14	attacker огрел противника дубиной. defender поражен таким теплым приемом	0
38	attacker вскружил противнику голову, дал от ворот поворот и вывел из себя.	0
3	attacker просто бьет противника. Комментатор в недоумении	0
2	После удара по голове attacker забывает даже имя противника, но готов продолжать, потому что руки-то помнят	0
6	attacker открывает древний манускрипт и нараспев зачитывает: "..для любого эпсилон больше нуля существует дельта больше нуля.." defender в ужасе затыкает уши, но мозг уже не спасти	0
15	Врезав противнику от души, attacker с удовольствием наблюдает свой рост в пантеоне неблагодарности	0
17	Фрустированный attacker прикладывает к лицу противника кисти рук, предварительно сжав их в кулаки	0
18	Получив жизненный опыт и по челюсти, defender падает с умным видом	0
24	attacker трогает врага холодными руками. defender мерзнет и ежится	0
36	attacker повелительно наклоняет противника в страдательном залоге.	0
\.


                                                                                                                                                                                                                                                                               2902.dat                                                                                            0000600 0004000 0002000 00000000016 13764676135 0014265 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        5	10	5	0
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  2898.dat                                                                                            0000600 0004000 0002000 00000000040 13764676135 0014300 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        10	log	23	23	lag
11	123			
\.


                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                restore.sql                                                                                         0000600 0004000 0002000 00000017152 13764676135 0015414 0                                                                                                    ustar 00postgres                        postgres                        0000000 0000000                                                                                                                                                                        --
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

-- Dumped from database version 12.3
-- Dumped by pg_dump version 12.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE trrp4db;
--
-- Name: trrp4db; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE trrp4db WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'Russian_Russia.1251' LC_CTYPE = 'Russian_Russia.1251';


ALTER DATABASE trrp4db OWNER TO postgres;

\connect trrp4db

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: heroes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.heroes (
    id integer NOT NULL,
    id_user integer NOT NULL,
    name text NOT NULL,
    health integer NOT NULL
);


ALTER TABLE public.heroes OWNER TO postgres;

--
-- Name: heroes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.heroes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.heroes_id_seq OWNER TO postgres;

--
-- Name: heroes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.heroes_id_seq OWNED BY public.heroes.id;


--
-- Name: phrases; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.phrases (
    id integer NOT NULL,
    phrase text NOT NULL,
    type integer NOT NULL
);


ALTER TABLE public.phrases OWNER TO postgres;

--
-- Name: phrases_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.phrases_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.phrases_id_seq OWNER TO postgres;

--
-- Name: phrases_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.phrases_id_seq OWNED BY public.phrases.id;


--
-- Name: statistic; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statistic (
    id integer NOT NULL,
    id_user integer NOT NULL,
    wins integer NOT NULL,
    loses integer NOT NULL
);


ALTER TABLE public.statistic OWNER TO postgres;

--
-- Name: statistic_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.statistic_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.statistic_id_seq OWNER TO postgres;

--
-- Name: statistic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.statistic_id_seq OWNED BY public.statistic.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    login text NOT NULL,
    hash text NOT NULL,
    salt text NOT NULL,
    nickname text NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: heroes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.heroes ALTER COLUMN id SET DEFAULT nextval('public.heroes_id_seq'::regclass);


--
-- Name: phrases id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phrases ALTER COLUMN id SET DEFAULT nextval('public.phrases_id_seq'::regclass);


--
-- Name: statistic id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statistic ALTER COLUMN id SET DEFAULT nextval('public.statistic_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: heroes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.heroes (id, id_user, name, health) FROM stdin;
\.
COPY public.heroes (id, id_user, name, health) FROM '$$PATH$$/2900.dat';

--
-- Data for Name: phrases; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.phrases (id, phrase, type) FROM stdin;
\.
COPY public.phrases (id, phrase, type) FROM '$$PATH$$/2904.dat';

--
-- Data for Name: statistic; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.statistic (id, id_user, wins, loses) FROM stdin;
\.
COPY public.statistic (id, id_user, wins, loses) FROM '$$PATH$$/2902.dat';

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, login, hash, salt, nickname) FROM stdin;
\.
COPY public.users (id, login, hash, salt, nickname) FROM '$$PATH$$/2898.dat';

--
-- Name: heroes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.heroes_id_seq', 1, false);


--
-- Name: phrases_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.phrases_id_seq', 44, true);


--
-- Name: statistic_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.statistic_id_seq', 5, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 11, true);


--
-- Name: heroes heroes_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.heroes
    ADD CONSTRAINT heroes_pk PRIMARY KEY (id);


--
-- Name: phrases phrases_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.phrases
    ADD CONSTRAINT phrases_pk PRIMARY KEY (id);


--
-- Name: statistic statistic_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statistic
    ADD CONSTRAINT statistic_pk PRIMARY KEY (id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- Name: users_login_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_login_uindex ON public.users USING btree (login);


--
-- Name: heroes heroes_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.heroes
    ADD CONSTRAINT heroes_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id);


--
-- Name: statistic statistic_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statistic
    ADD CONSTRAINT statistic_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      