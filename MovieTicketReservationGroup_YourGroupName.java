import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class InvalidMovieCodeException extends Exception {
    public InvalidMovieCodeException(String message) {
        super(message);
    }
}

class OverbookingException extends Exception {
    public OverbookingException(String message) {
        super(message);
    }
}

class Movie {
    String code;
    String title;
    List<String> showtimes;

    public Movie(String code, String title, List<String> showtimes) {
        this.code = code;
        this.title = title;
        this.showtimes = showtimes;
    }
}

class ReservationSystem {
    private List<Movie> movies;
    private Map<String, Integer> availableSeats;

    public ReservationSystem(String csvFilePath) {
        movies = new ArrayList<>();
        availableSeats = new HashMap<>();
        initializeMovies(csvFilePath);
    }

    private void initializeMovies(String csvFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line = br.readLine(); // Skip the header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 6) continue; // Skip invalid lines
                String code = values[0];
                String title = values[1];
                String showtime = values[3];
                int availableSeatsCount = Integer.parseInt(values[5]);

                // Add movie if not already added
                Movie movie = findMovie(code);
                if (movie == null) {
                    movie = new Movie(code, title, new ArrayList<>());
                    movies.add(movie);
                }
                movie.showtimes.add(showtime);

                // Initialize available seats for the showtime
                availableSeats.put(code + "_" + showtime, availableSeatsCount);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing seat count: " + e.getMessage());
        }
    }

    public void bookTickets(String movieCode, String showtime, int tickets) throws InvalidMovieCodeException, OverbookingException {
        Movie movie = findMovie(movieCode);
        if (movie == null) {
            throw new InvalidMovieCodeException("Invalid movie code: " + movieCode);
        }
        String key = movieCode + "_" + showtime;
        if (!availableSeats.containsKey(key)) {
            throw new InvalidMovieCodeException("Invalid showtime for movie: " + showtime);
        }
        if (availableSeats.get(key) < tickets) {
            throw new OverbookingException("Not enough seats available for " + movie.title + " at " + showtime);
        }
        availableSeats.put(key, availableSeats.get(key) - tickets);
        System.out.println("Successfully booked " + tickets + " tickets for " + movie.title + " at " + showtime);
    }

    private Movie findMovie(String movieCode) {
        for (Movie movie : movies) {
            if (movie.code.equals(movieCode)) {
                return movie;
            }
        }
        return null;
    }
}

public class MovieTicketReservationGroup_YourGroupName {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String csvFilePath = "Movie Reservation Dataset.csv"; // Specify the path to your CSV file
        ReservationSystem reservationSystem = new ReservationSystem(csvFilePath);

        try {
            System.out.print("Enter movie code: ");
            String movieCode = scanner.nextLine();
            System.out.print("Enter showtime: ");
            String showtime = scanner.nextLine();
            System.out.print("Enter number of tickets: ");
            if (!scanner.hasNextInt()) {
                throw new InputMismatchException("Invalid input for number of tickets. Please enter an integer.");
            }
            int tickets = scanner.nextInt();

            reservationSystem.bookTickets(movieCode, showtime, tickets);
        } catch (InvalidMovieCodeException | OverbookingException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}