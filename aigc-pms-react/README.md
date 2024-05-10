```shell

    #upload 1
    cd ..
    docker build --platform linux/amd64 -t beamstark/aigc-pms-web-react:2.1 .
    
    #upload 2
    docker push beamstark/aigc-pms-web-react:2.1
    
    #download1
    docker pull beamstark/aigc-pms-web-react:2.1
    
    #download2
    docker run -d -p 3000:3000 --name aigc-pms-web-react_2.0 beamstark/aigc-pms-web-react:2.0
```

