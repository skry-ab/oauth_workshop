import {useState} from 'react';
import {Route, Routes} from 'react-router-dom';
import MainPage from './MainPage';


function App() {
  const [token, setToken] = useState<string>();
  return (
    <Routes>
      <Route path="/" element={<MainPage token={token} />} />
    </Routes>
  );
}

export default App;
