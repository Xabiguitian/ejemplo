package es.udc.ws.movies.model.movie;

import java.time.LocalDateTime;

public class Movie {

	private Long movieId;
	private String title;
	private short runtime;
	private String description;
	private float price;
	private LocalDateTime creationDate;

	public Movie(String title, short runtime, String description, float price) {
		this.title = title;
		this.runtime = runtime;
		this.description = description;
		this.price = price;
	}

	public Movie(Long movieId, String title, short runtime, String description, float price) {
		this(title, runtime, description, price);
		this.movieId = movieId;
	}

	public Movie(Long movieId, String title, short runtime, String description, float price, LocalDateTime creationDate) {
		this(movieId, title, runtime, description, price);
		this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
	}

	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long id) {
		this.movieId = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public short getRuntime() {
		return runtime;
	}

	public void setRuntime(short runtime) {
		this.runtime = runtime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((movieId == null) ? 0 : movieId.hashCode());
		result = prime * result + Float.floatToIntBits(price);
		result = prime * result + runtime;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (movieId == null) {
			if (other.movieId != null)
				return false;
		} else if (!movieId.equals(other.movieId))
			return false;
		if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price))
			return false;
		if (runtime != other.runtime)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}