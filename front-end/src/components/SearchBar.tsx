import { useState } from "react";
import {
  Autocomplete,
  Box,
  Button,
  Collapse,
  Paper,
  TextField,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import ClearIcon from "@mui/icons-material/Clear";
import TuneIcon from "@mui/icons-material/Tune";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";

import { Author, Genre } from "../models/Book";

interface Props {
  title: string;
  authors: Author[];
  genres: Genre[];
  selectedAuthors: Author[];
  selectedGenres: Genre[];

  onTitleChange: (value: string) => void;
  onAuthorsChange: (value: Author[]) => void;
  onGenresChange: (value: Genre[]) => void;

  onSearch: () => void;
  onClear: () => void;
}

export default function SearchBar({
  title,
  authors,
  genres,
  selectedAuthors,
  selectedGenres,
  onTitleChange,
  onAuthorsChange,
  onGenresChange,
  onSearch,
  onClear,
}: Props) {
  const [showFilters, setShowFilters] = useState(false);

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Box display="flex" flexDirection="column" gap={2}>
        <TextField
          label="Search by title"
          placeholder="Enter a book title..."
          value={title}
          onChange={(e) => onTitleChange(e.target.value)}
          fullWidth
        />

        <Box display="flex" justifyContent="flex-start">
          <Button
            startIcon={<TuneIcon />}
            endIcon={
              showFilters ? (
                <ExpandLessIcon />
              ) : (
                <ExpandMoreIcon />
              )
            }
            onClick={() => setShowFilters(!showFilters)}
          >
            {showFilters ? "Hide filters" : "More filters"}
          </Button>
        </Box>

        <Collapse in={showFilters}>
          <Box display="flex" flexDirection="column" gap={2} mt={1}>
            <Autocomplete
              multiple
              options={authors}
              value={selectedAuthors}
              getOptionLabel={(option) => option.name}
              isOptionEqualToValue={(o, v) => o.id === v.id}
              onChange={(_, value) => onAuthorsChange(value)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Authors"
                  placeholder="Select author(s)"
                />
              )}
            />

            <Autocomplete
              multiple
              options={genres}
              value={selectedGenres}
              getOptionLabel={(option) => option.name}
              isOptionEqualToValue={(o, v) => o.id === v.id}
              onChange={(_, value) => onGenresChange(value)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Genres"
                  placeholder="Select genre(s)"
                />
              )}
            />
          </Box>
        </Collapse>

        <Box display="flex" justifyContent="flex-end" gap={2}>
          <Button
            variant="outlined"
            startIcon={<ClearIcon />}
            onClick={onClear}
          >
            Clear
          </Button>

          <Button
            variant="contained"
            startIcon={<SearchIcon />}
            onClick={onSearch}
          >
            Search
          </Button>
        </Box>
      </Box>
    </Paper>
  );
}