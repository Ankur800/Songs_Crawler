package project.songsfetcher.songsCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import project.songsfetcher.database.GenericDB;
import project.songsfetcher.song.Song;
import project.songsfetcher.utility.FileUtility;
import project.songsfetcher.utility.HttpURLConnectionExample;
import project.songsfetcher.utility.TaskManager;
import java.util.ArrayList;

public class SongsFetcher implements Runnable {

    private String link;
    private String basicUrl = "https://songspk.mobi";
    public SongsFetcher(String link) {
        this.link = link;
    }

    public void run(){

        //Variables
        ArrayList<String> childLinks = new ArrayList<String>();

        try {
            //Collecting HTML response for parent URL
            String songsResponseHTML = HttpURLConnectionExample.sendGet(link);

            //Jsoup Parsing
            Document document = Jsoup.parse(songsResponseHTML, basicUrl);
            Elements linksOnThisPage = document.body().select(".thumb-image");

            for(Element link : linksOnThisPage){
                String tempChildUrl = link.getElementsByAttribute("href").attr("href");
                childLinks.add(getModifiedUrl(tempChildUrl));
            }

            for(String childLink : childLinks){

                Song song = new Song();

                song.parent_link = link;
                song.link = childLink;

                try {
                    String childPageResponseHTML = HttpURLConnectionExample.sendGet(childLink);

                    Document childDocument = Jsoup.parse(childPageResponseHTML, basicUrl);
                    Elements childElements = childDocument.body().select(".page-meta-wrapper");

                    for (Element childElement : childElements) {


                        //Song Name
                        String tempSongNameData = childElement.getElementsByAttribute("alt").attr("alt");
                        String[] tempSongMod = tempSongNameData.split("Mp3", 2);
                        String tempSongName = tempSongMod[0].trim();
                        song.song_name = tempSongName;


                        //Image URL
                        String tempImageURL = childElement.getElementsByAttribute("src").attr("src");
                        song.image_url = tempImageURL;


                        String[] firstStringOfSong = tempSongName.split(" ", 2);
                        String firstWordOfSong = firstStringOfSong[0];


                        String allData = childElement.text();
                        String currentDataToBeUsed = allData;

                        //Raw Data
                        if (allData.contains("Album")) {
                            String[] tempRawData = currentDataToBeUsed.split("Album", 2);
                            currentDataToBeUsed = tempRawData[1];
                        }

                        //Album
                        if (allData.contains("Duration")) {
                            String[] tempAlbumData = currentDataToBeUsed.split("Duration", 2);
                            String tempAlbum = tempAlbumData[0].trim();
                            song.album = tempAlbum;
                            currentDataToBeUsed = tempAlbumData[1];
                        }

                        //Duration
                        if (currentDataToBeUsed.contains("Singer")) {
                            String[] tempDurationData = currentDataToBeUsed.split("Singer", 2);
                            String tempDuration = tempDurationData[0].trim();
                            song.duration = tempDuration;
                            currentDataToBeUsed = tempDurationData[1];
                        }

                        //Music director and Lyricist
                        if (currentDataToBeUsed.contains("Music Director")) {
                            String tempSingerData[] = currentDataToBeUsed.split("Music Director", 2);
                            String tempSinger = tempSingerData[0].trim();
                            song.singers = tempSinger;


                            if (tempSingerData[1].contains("Lyricist")) {
                                String[] tempMusicDirectorData = tempSingerData[1].split("Lyricist", 2);
                                String tempMusicDirector = tempMusicDirectorData[0].trim();
                                song.music_director = tempMusicDirector;

                                String[] tempLyricistData = tempMusicDirectorData[1].split(firstWordOfSong, 2);
                                String tempLyricist = tempLyricistData[0].trim();
                                song.lyricist = tempLyricist;

                            } else {
                                String[] tempMusicDirectorData = tempSingerData[1].split(firstWordOfSong, 2);
                                String tempMusicDirector = tempMusicDirectorData[0].trim();
                                song.music_director = tempMusicDirector;

                            }
                        } else {
                            if (currentDataToBeUsed.contains("Lyricist")) {
                                String tempSingerData[] = currentDataToBeUsed.split("Lyricist", 2);
                                String tempSinger = tempSingerData[0].trim();
                                song.singers = tempSinger;

                                String[] tempLyricistData = tempSingerData[1].split(firstWordOfSong, 2);
                                String tempLyricist = tempLyricistData[0].trim();
                                song.lyricist = tempLyricist;

                            } else {
                                String[] tempSingerData = currentDataToBeUsed.split(firstWordOfSong, 2);
                                String tempSinger = tempSingerData[0].trim();
                                song.singers = tempSinger;

                            }
                        }

                        //Download link
                        Elements childElements2 = childDocument.body().select(".col-body > *");
                        int state = 0;
                        ArrayList<String> downloadLinks = new ArrayList<String>();
                        for (Element childElement2 : childElements2) {
                            String tempD = childElement2.getElementsByAttribute("href").attr("href");
                            if (tempD.contains("http")) {
                                downloadLinks.add(tempD);
                            }
                            state++;
                            if (state == 2)
                                break;
                        }

                        //128kbps link
                        song.download_128 = downloadLinks.get(0);


                        //320kbps link
                        if (downloadLinks.size() == 2) {
                            song.download_320 = downloadLinks.get(1);
                        } else {
                            song.download_320 = null;
                        }

                        //new GenericDB<Song>().addRow(tech.codingclub.helix.tables.Song.SONG, song);

                        System.out.println(song.song_name);

                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getModifiedUrl(String tempChildUrl) {
        return basicUrl + tempChildUrl;
    }

    public static void main(String[] args) {
        String filePath = "/home/ankur/coding-club-FSD/crawlsonglinks.txt";
        ArrayList<String> links = readAllLinksFromFile(filePath);

        for(String link : links) {
            TaskManager taskManager = new TaskManager(20);
            taskManager.waitTillQueueIsFreeAndAddTask(new SongsFetcher(link));
        }
    }

    private static ArrayList<String> readAllLinksFromFile(String filePath) {
        ArrayList<String> links = FileUtility.readFileAsList(filePath);
        return links;
    }
}
